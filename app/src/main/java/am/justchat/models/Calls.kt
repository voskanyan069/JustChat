package am.justchat.models

import am.justchat.states.CallState

class Calls(val profileUsername: String, val callMessage: String, val callState: CallState, val profileImage: String)