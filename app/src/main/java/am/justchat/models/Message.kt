package am.justchat.models

import am.justchat.states.MessageSenderState

data class Message(val viewType: MessageSenderState, val message: String, val sender: MessageUser, val time: Long)