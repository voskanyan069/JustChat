package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.CallsAdapter
import am.justchat.models.Call
import am.justchat.states.CallState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CallsFragment : Fragment() {
    private lateinit var callsList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calls, container, false)

        val calls = listOf(
            Call("Username_1", "24 minute 5 seconds", CallState.INCOMING, "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
            Call("Username_2", "12 minute 15 seconds", CallState.OUTGOING, "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
            Call("Username_3", "1 hour 21 minute", CallState.OUTGOING, "https://images.unsplash.com/photo-1614705755374-538591a6f8f4?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=700&q=80"),
            Call("Username_4", "Call was missed", CallState.MISSED, "https://images.unsplash.com/photo-1614676314170-3eb1d98d0a25?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Call("Username_5", "Call was unanswered", CallState.UNANSWERED, "https://images.unsplash.com/photo-1614631446449-e2c7a0cc1428?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
        )

        callsList = root.findViewById(R.id.calls_list)
        callsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        fillCallsList(calls)

        return root
    }

    private fun fillCallsList(data: List<Call>) {
        callsList.adapter = CallsAdapter(data)
    }
}