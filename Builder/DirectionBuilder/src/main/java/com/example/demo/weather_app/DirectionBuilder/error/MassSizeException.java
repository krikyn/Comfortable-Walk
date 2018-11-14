package com.example.demo.weather_app.DirectionBuilder.error;

// daba название эксепшена вызывает вопросы. Mass - это Massiv?
// daba назначение тоже не особо понятно. Что мешает бросать сам OOB Exception?
// daba пакет с эксепшенами лучше назвать exception, потому что error - это конкретный вариант эксепшена
public class MassSizeException extends ArrayIndexOutOfBoundsException {
    public MassSizeException(String s) {
        super(s);
    }
}
