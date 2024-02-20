package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.Note

object NoteComparator : Comparator<Note> {

    private fun dateToInt(date: String): Int {
        var dateForm = ""
        date.forEach { if (it != '.') dateForm += it }
        val num = dateForm.toIntOrNull()
        var newDate: Int = Int.MAX_VALUE
        (num ?: return newDate).let {
            val year = num % 10000
            val month = num / 10000 % 100
            val day = num / 1000000
            newDate = year * 10000 + month * 100 + day
        }
        return newDate
    }

    override fun compare(ln: Note, rn: Note): Int {
        return if (ln.priority < rn.priority || (ln.priority == rn.priority &&
                    dateToInt(ln.date) < dateToInt(rn.date))
        ) -1 else if (ln.priority > rn.priority) 1 else 0
    }
}