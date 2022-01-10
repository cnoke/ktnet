



# Kotlin 协程+Retrofit 最优雅的使用

## 1.简介

Retrofit对协程的支持非常的简陋。在kotlin中使用不符合kotlin的优雅

```kotlin
interface TestServer {
    @GET("banner/json")
    suspend fun banner(): ApiResponse<List<Banner>>
}

//实现并行捕获异常的网络请求
 fun oldBanner(){
        viewModelScope.launch {
            //传统模式使用retrofit需要try catch

            val bannerAsync1 = async {
                var result : ApiResponse<List<Banner>>? = null
                kotlin.runCatching {
                   service.banner()
                }.onFailure {
                    Log.e("banner",it.toString())
                }.onSuccess {
                    result = it 
                }
                result
            }

            val bannerAsync2 = async {
                var result : ApiResponse<List<Banner>>? = null
                kotlin.runCatching {
                    service.banner()
                }.onFailure {
                    Log.e("banner",it.toString())
                }.onSuccess {
                    result = it
                }
                result
            }

            bannerAsync1.await()
            bannerAsync2.await()
        }
    }
```

一层嵌套一层，属实无法忍受。kotlin应该一行代码解决问题，才符合kotlin的优雅

使用本框架后

```kotlin
interface TestServer {
    @GET("banner/json")
    suspend fun awaitBanner(): Await<List<Banner>>
}

	//实现并行捕获异常的网络请求
fun parallel(){
     viewModelScope.launch {
     val awaitBanner1 = service.awaitBanner().tryAsync(this)
     val awaitBanner2 = service.awaitBanner().tryAsync(this)

      //两个接口一起调用
      awaitBanner1.await()
      awaitBanner2.await()
   }
}
```



## 2.源码地址

[GitHub](https://github.com/cnoke/ktnet)

## 3.查看Retrofit源码

先看Retrofit create方法

```kotlin
public <T> T create(final Class<T> service) {
    validateServiceInterface(service);
    return (T)
        Proxy.newProxyInstance(
            service.getClassLoader(),
            new Class<?>[] {service},
            new InvocationHandler() {
              private final Platform platform = Platform.get();
              private final Object[] emptyArgs = new Object[0];

              @Override
              public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
                  throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                  return method.invoke(this, args);
                }
                args = args != null ? args : emptyArgs;
                return platform.isDefaultMethod(method)
                    ? platform.invokeDefaultMethod(method, service, proxy, args)
                    : loadServiceMethod(method).invoke(args);//具体调用
              }
            });
  }
```

loadServiceMethod(method).invoke(args)进入这个方法看具体调用

![20220110110008.png](https://gitee.com/cnoke_301/readmeimg/raw/master/replacefilename-studio-plugin/20220110110008.png)

![20220110110243.png](https://gitee.com/cnoke_301/readmeimg/raw/master/replacefilename-studio-plugin/20220110110243.png)

我们查看suspenForResponse中的adapt

```kotlin
@Override
    protected Object adapt(Call<ResponseT> call, Object[] args) {
      call = callAdapter.adapt(call);//如果用户不设置callAdapterFactory就使用DefaultCallAdapterFactory

      //noinspection unchecked Checked by reflection inside RequestFactory.
      Continuation<Response<ResponseT>> continuation =
          (Continuation<Response<ResponseT>>) args[args.length - 1];

      // See SuspendForBody for explanation about this try/catch.
      try {
        return KotlinExtensions.awaitResponse(call, continuation);
      } catch (Exception e) {
        return KotlinExtensions.suspendAndThrow(e, continuation);
      }
    }
  }
```

后面直接交给协程去调用call。具体的okhttp调用在DefaultCallAdapterFactory。或者用户自定义的callAdapterFactory中

因此我们这边可以自定义CallAdapterFactory在调用后不进行网络请求的访问，在用户调用具体方法时候再进行网络请求访问。

## 4.自定义CallAdapterFactory

Retrofit在调用后直接进行了网络请求，因此很不好操作。我们把网络请求的控制权放在我们手里，就能随意操作。

```kotlin
class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        //检查returnType是否是Call<T>类型的
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "$returnType must be parameterized. Raw types are not supported" }
        //取出Call<T> 里的T，检查是否是Await<T>
        val apiResultType = getParameterUpperBound(0, returnType)
        // 如果不是 Await 则不由本 CallAdapter.Factory 处理 兼容正常模式
        if (getRawType(apiResultType) != Await::class.java) return null
        check(apiResultType is ParameterizedType) { "$apiResultType must be parameterized. Raw types are not supported" }

        //取出Await<T>中的T 也就是API返回数据对应的数据类型
//        val dataType = getParameterUpperBound(0, apiResultType)

        return ApiResultCallAdapter<Any>(apiResultType)
    }

}

class ApiResultCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<Await<T>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<Await<T>> {
        return ApiResultCall(call)
    }
}

class ApiResultCall<T>(private val delegate: Call<T>) : Call<Await<T>> {
    /**
     * 该方法会被Retrofit处理suspend方法的代码调用，并传进来一个callback,如果你回调了callback.onResponse，那么suspend方法就会成功返回
     * 如果你回调了callback.onFailure那么suspend方法就会抛异常
     *
     * 所以我们这里的实现是回调callback.onResponse,将okhttp的call delegate
     */
    override fun enqueue(callback: Callback<Await<T>>) {
        //将okhttp call放入AwaitImpl直接返回，不做网络请求。在调用AwaitImpl的await时才真正开始网络请求
        callback.onResponse(this@ApiResultCall, Response.success(delegate.toResponse()))
    }
}


internal class AwaitImpl<T>(
    private val call : Call<T>,
) : Await<T> {

    override suspend fun await(): T {

        return try {
            call.await()
        } catch (t: Throwable) {
            throw t
        }
    }
}
```

通过上面自定义callAdapter后，我们延迟了网络请求，在调用Retrofit后并不会请求网络，只会将网络请求所需要的call的放入await中。

```kotlin
	@GET("banner/json")
    suspend fun awaitBanner(): Await<List<Banner>>
```

我们拿到的Await<List<Banner>>并没有做网络请求。在这个实体类中包含了okHttp的call。

这时候我们可以定义如下方法就能捕获异常

```kotlin
suspend fun <T> Await<T>.tryAsync(
    scope: CoroutineScope,
    onCatch: ((Throwable) -> Unit)? = null,
    context: CoroutineContext = SupervisorJob(scope.coroutineContext[Job]),
    start: CoroutineStart = CoroutineStart.DEFAULT
): Deferred<T?> = scope.async(context, start) {
    try {
        await()
    } catch (e: Throwable) {
        onCatch?.invoke(e)
        null
    }
}
```

同样并行捕获异常的请求，就可以通过如下方式调用，优雅简洁了很多
```kotlin
 	/**
     * 并行 async
     */
    fun parallel(){
        viewModelScope.launch {
            val awaitBanner1 = service.awaitBanner().tryAsync(this)
            val awaitBanner2 = service.awaitBanner().tryAsync(this)

            //两个接口一起调用
            awaitBanner1.await()
            awaitBanner2.await()
        }
    }
```

这时候我们发现网络请求成功了，解析数据失败。因为我们在数据外面套了一层await。肯定无法解析成功。

本着哪里错误解决哪里的思路，我们自定义Gson解析

## 5.自定义Gson解析

```kotlin
class GsonConverterFactory private constructor(private var responseCz : Class<*>,var responseConverter : GsonResponseBodyConverter, private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        var adapter : TypeAdapter<*>? = null
        //检查是否是Await<T>
        if (Utils.getRawType(type) == Await::class.java && type is ParameterizedType){
            //取出Await<T>中的T
            val awaitType =  Utils.getParameterUpperBound(0, type)
            if(awaitType != null){
                adapter = gson.getAdapter(TypeToken.get(ParameterizedTypeImpl[responseCz,awaitType]))
            }
        }
        //不是awiat正常解析，兼容正常模式
        if(adapter == null){
            adapter= gson.getAdapter(TypeToken.get(ParameterizedTypeImpl[responseCz,type]))
        }
        return responseConverter.init(gson, adapter!!)
    }
}

class MyGsonResponseBodyConverter : GsonResponseBodyConverter() {

    override fun convert(value: ResponseBody): Any {
        val jsonReader = gson.newJsonReader(value.charStream())
        val data = adapter.read(jsonReader) as ApiResponse<*>
        val t = data.data

        val listData = t as? ApiPagerResponse<*>
        if (listData != null) {
            //如果返回值值列表封装类，且是第一页并且空数据 那么给空异常 让界面显示空
            if (listData.isRefresh() && listData.isEmpty()) {
                throw ParseException(NetConstant.EMPTY_CODE, data.errorMsg)
            }
        }

        // errCode 不等于 SUCCESS_CODE，抛出异常
        if (data.errorCode != NetConstant.SUCCESS_CODE) {
            throw ParseException(data.errorCode, data.errorMsg)
        }

        return t!!
    }

}
```

## 6.本框架使用

#### 添加依赖
[![Download](https://maven-badges.herokuapp.com/maven-central/io.github.cnoke.ktnet/api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.cnoke.ktnet/api)

```groovy
implementation "io.github.cnoke.ktnet:api:?"
```

写一个网络请求数据基类

```kotlin
open class ApiResponse<T>(
    var data: T? = null,
    var errorCode: String = "",
    var errorMsg: String = ""
)
```

实现com.cnoke.net.factory.GsonResponseBodyConverter

```kotlin
class MyGsonResponseBodyConverter : GsonResponseBodyConverter() {

    override fun convert(value: ResponseBody): Any {
        val jsonReader = gson.newJsonReader(value.charStream())
        val data = adapter.read(jsonReader) as ApiResponse<*>
        val t = data.data

        val listData = t as? ApiPagerResponse<*>
        if (listData != null) {
            //如果返回值值列表封装类，且是第一页并且空数据 那么给空异常 让界面显示空
            if (listData.isRefresh() && listData.isEmpty()) {
                throw ParseException(NetConstant.EMPTY_CODE, data.errorMsg)
            }
        }

        // errCode 不等于 SUCCESS_CODE，抛出异常
        if (data.errorCode != NetConstant.SUCCESS_CODE) {
            throw ParseException(data.errorCode, data.errorMsg)
        }

        return t!!
    }

}
```

进行网络请求

```kotlin
interface TestServer {
    @GET("banner/json")
    suspend fun awaitBanner(): Await<List<Banner>>
}

val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeadInterceptor())
            .addInterceptor(LogInterceptor())
            .build()

val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.wanandroid.com/")
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(ApiResponse::class.java,MyGsonResponseBodyConverter()))
            .build()
val service: TestServer = retrofit.create(TestServer::class.java)
lifecycleScope.launch {
       val banner = service.awaitBanner().await()
}
```

异步请求同步请求，异常捕获参考如下try开头的会捕获异常，非try开头不会捕获。

```kotlin
fun banner(){
    lifecycleScope.launch {
        //单独处理异常 tryAwait会处理异常，如果异常返回空
        val awaitBanner = service.awaitBanner().tryAwait()
        awaitBanner?.let {
            for(banner in it){
                Log.e("awaitBanner",banner.title)
            }
        }

        /**
         * 不处理异常 异常会直接抛出，统一处理
         */
        val awaitBannerError = service.awaitBanner().await()
    }
}

/**
 * 串行 await
 */
fun serial(){
    lifecycleScope.launch {
        //先调用第一个接口await
        val awaitBanner1 = service.awaitBanner().await()
        //第一个接口完成后调用第二个接口
        val awaitBanner2 = service.awaitBanner().await()
    }
}

/**
 * 并行 async
 */
fun parallel(){
    lifecycleScope.launch {
        val awaitBanner1 = service.awaitBanner().async(this)
        val awaitBanner2 = service.awaitBanner().async(this)

        //两个接口一起调用
        awaitBanner1.await()
        awaitBanner2.await()
    }
}
```

