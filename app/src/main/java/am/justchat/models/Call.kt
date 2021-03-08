package am.justchat.models

import am.justchat.states.CallState

class Call(val profileUsername: String, val callTime: String, val callState: CallState, val profileImage: String)