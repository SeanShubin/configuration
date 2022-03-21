package com.seanshubin.configuration.domain

import java.lang.NumberFormatException

enum class Type {
    STRING {
        override fun fromString(s: String):Any = s
    },
    BYTE {
        override fun fromString(s: String): Any =
            try {
                java.lang.Byte.parseByte(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    SHORT {
        override fun fromString(s: String): Any =
            try {
                java.lang.Short.parseShort(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    INT {
        override fun fromString(s: String): Any =
            try {
                Integer.parseInt(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    LONG {
        override fun fromString(s: String): Any =
            try {
                java.lang.Long.parseLong(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    FLOAT {
        override fun fromString(s: String): Any =
            try {
                java.lang.Float.parseFloat(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    DOUBLE {
        override fun fromString(s: String): Any =
            try {
                java.lang.Double.parseDouble(s)
            } catch(ex: NumberFormatException){
                s
            }
    },
    CHAR {
        override fun fromString(s: String): Any =
            if(s.length == 1) s[0]
            else throw RuntimeException("Expected a single code unit, got ${s.length}")
    },
    BOOLEAN {
        override fun fromString(s: String): Any =
            try {
                java.lang.Boolean.parseBoolean(s)
            } catch(ex: NumberFormatException){
                s
            }
    };

    abstract fun fromString(s: String):Any
}
