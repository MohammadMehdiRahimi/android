package com.example.ui.core

fun String.toPersianNumber(): String {
    val persianNumbers = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map { if (it in '0'..'9') persianNumbers[it - '0'] else it }.joinToString("")
}

fun Int.toPersianNumber(): String = this.toString().toPersianNumber()
fun Long.toPersianNumber(): String = this.toString().toPersianNumber()
fun Number.toPersianNumber(): String = this.toString().toPersianNumber()

fun String.toPersianNumberSafe(): String {
    val builder = StringBuilder()
    var inTag = false
    var inCommand = false
    var inMath = false
    var mathDelimiter = ""
    var i = 0
    val persianNumbers = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    
    while (i < this.length) {
        val c = this[i]
        val nextC = if (i + 1 < this.length) this[i + 1] else null

        if (!inMath && !inTag) {
            if (c == '$' && nextC == '$') {
                inMath = true
                mathDelimiter = "$$"
                builder.append("$$")
                i += 2
                continue
            } else if (c == '$') {
                inMath = true
                mathDelimiter = "$"
                builder.append("$")
                i += 1
                continue
            } else if (c == '\\' && nextC == '[') {
                inMath = true
                mathDelimiter = "\\["
                builder.append("\\[")
                i += 2
                continue
            } else if (c == '\\' && nextC == '(') {
                inMath = true
                mathDelimiter = "\\("
                builder.append("\\(")
                i += 2
                continue
            } else if (c == '\\' && this.substring(i).startsWith("\\begin{")) {
                val endIdx = this.indexOf("}", i)
                if (endIdx != -1) {
                    val env = this.substring(i + 7, endIdx)
                    inMath = true
                    mathDelimiter = "\\end{$env}"
                    builder.append("\\begin{$env}")
                    i = endIdx + 1
                    continue
                }
            }
        } else if (inMath) {
            if (mathDelimiter == "$$" && c == '$' && nextC == '$') {
                inMath = false
                builder.append("$$")
                i += 2
                continue
            } else if (mathDelimiter == "$" && c == '$') {
                inMath = false
                builder.append("$")
                i += 1
                continue
            } else if (mathDelimiter == "\\[" && c == '\\' && nextC == ']') {
                inMath = false
                builder.append("\\]")
                i += 2
                continue
            } else if (mathDelimiter == "\\(" && c == '\\' && nextC == ')') {
                inMath = false
                builder.append("\\)")
                i += 2
                continue
            } else if (mathDelimiter.startsWith("\\end{") && c == '\\' && this.substring(i).startsWith(mathDelimiter)) {
                inMath = false
                builder.append(mathDelimiter)
                i += mathDelimiter.length
                continue
            }
        }

        if (!inMath) {
            if (c == '<') inTag = true
            if (c == '\\') inCommand = true
            if (inCommand && (c == ' ' || c == '\n' || c == '{' || c == '}')) {
                inCommand = false
            }
        }

        val isDigit = c in '0'..'9'
        if (isDigit && !inTag && !inCommand && !inMath) {
            builder.append(persianNumbers[c - '0'])
        } else {
            builder.append(c)
        }

        if (!inMath) {
            if (c == '>') inTag = false
        }
        i++
    }
    return builder.toString()
}
