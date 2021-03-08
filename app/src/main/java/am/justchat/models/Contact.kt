package am.justchat.models

import am.justchat.states.OnlineState

class Contact(val profileUsername: String, val profileOnlineState: OnlineState, val profileImage: String)