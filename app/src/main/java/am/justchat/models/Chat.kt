package am.justchat.models

import am.justchat.states.OnlineState

class Chat(val profileLogin: String, val profileUsername: String, val isOnline: OnlineState, val lastMessage: String, val profileImage: String)