package com.bethwelamkenya.app2

import com.bethwelamkenya.app2.models.Admin
import com.bethwelamkenya.app2.models.Member


class PassData {
    companion object {
        var admin: Admin? = null
        var resetAdmin: Admin? = null
        var message: String? = null
        var adminName: String? = null
        var firstTime = false
        var passedMember: Member? = null
    }
}