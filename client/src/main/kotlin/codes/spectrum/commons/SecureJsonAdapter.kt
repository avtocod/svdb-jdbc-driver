package codes.spectrum.commons

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class SecureJsonAdapter : TypeAdapter<String>() {
    override fun write(w : JsonWriter, value: String) {
        if (value.length == 0){
            w.jsonValue("")
        }else{
            w.jsonValue("********")
        }
    }

    override fun read(r : JsonReader): String {
        return r.nextString()
    }

}
