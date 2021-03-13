package am.justchat.models

import am.justchat.states.CallsState

data class Call(val profileUsername: String, val callTime: String, val callsState: CallsState, val profileImage: String)