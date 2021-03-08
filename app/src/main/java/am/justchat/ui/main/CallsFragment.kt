package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.CallsAdapter
import am.justchat.api.repos.CallsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Call
import am.justchat.models.Contact
import am.justchat.states.CallState
import am.justchat.states.OnlineState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Callback
import retrofit2.Response

class CallsFragment : Fragment() {
    private lateinit var callsList: RecyclerView
    private val callsArrayList = arrayListOf<Call>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calls, container, false)

        getCallsList()
        callsList = root.findViewById(R.id.calls_list)
        callsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        return root
    }

    private fun getCallsList() {
        if (CurrentUser.login != "null") {
            val callsRepo = CallsRepo.getInstance()
            callsRepo.callsService
                ?.getUserCalls(CurrentUser.login.toString())
                ?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: retrofit2.Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        val jsonParser = JsonParser()
                        val callsJsonStr = Gson().toJson(response.body())
                        val callsJson = jsonParser.parse(callsJsonStr).asJsonObject
                        val calls = callsJson.getAsJsonArray("calls")
                        for (i in 0..calls.size().minus(1)) {
                            val callJson: JsonObject = calls.get(i).asJsonObject
                            val thisCall = Call(
                                profileUsername = callJson.get("username").asString,
                                callState = when (callJson.get("call_status").asString) {
                                    "incoming" -> CallState.INCOMING
                                    "outgoing" -> CallState.OUTGOING
                                    "unanswered" -> CallState.UNANSWERED
                                    else -> CallState.MISSED
                                },
                                callTime = callJson.get("call_time").asString,
                                profileImage = callJson.get("profile_image").asString
                            )
                            callsArrayList.add(thisCall)
                        }
                        fillCallsList(callsArrayList)
                    }

                    override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {}
                })
        }
    }

    private fun fillCallsList(data: List<Call>) {
        callsList.adapter = CallsAdapter(data)
    }
}