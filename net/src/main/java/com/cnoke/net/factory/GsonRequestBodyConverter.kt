package com.cnoke.net.factory

import okhttp3.RequestBody
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset

class GsonRequestBodyConverter : Converter<Any, RequestBody> {


    protected lateinit var gson: Gson
    protected lateinit var adapter: TypeAdapter<Any>

    fun init(gson: Gson,adapter: TypeAdapter<Any>): GsonRequestBodyConverter {
        this.gson = gson
        this.adapter = adapter
        return this
    }

    @Throws(IOException::class)
    override fun convert(value: Any): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }
}
