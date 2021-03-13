package am.justchat.models

import am.justchat.states.OnlineState

data class Contact(val profileUsername: String, val profileOnlineState: OnlineState, val profileImage: String)