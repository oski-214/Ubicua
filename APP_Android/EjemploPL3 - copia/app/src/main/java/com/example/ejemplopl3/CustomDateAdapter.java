package com.example.ejemplopl3;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateAdapter extends TypeAdapter<Date> {

    // Definimos los dos formatos posibles que podría tener la fecha
    private final SimpleDateFormat format1 = new SimpleDateFormat("MMM d, yyyy, h:mm:ss a", Locale.US);

    // Y un formato más estándar, por si acaso
    private final SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Al escribir, usamos un formato estándar
            out.value(format2.format(value));
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String dateStr = in.nextString();

        
        dateStr = dateStr.replace('\u202F', ' ');

        // Intenta analizar con el primer formato
        try {
            return format1.parse(dateStr);
        } catch (ParseException e) {
            // Si falla, intenta con el segundo formato
            try {
                return format2.parse(dateStr);
            } catch (ParseException e2) {
                // Si ambos fallan, lanza una excepción
                throw new IOException("Failed to parse date: " + dateStr, e2);
            }
        }
    }
}
