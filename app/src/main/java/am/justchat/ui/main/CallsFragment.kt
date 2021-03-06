package am.justchat.ui.main

import am.justchat.R
import am.justchat.adapters.CallsAdapter
import am.justchat.api.Config
import am.justchat.api.repos.CallsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Call
import am.justchat.states.CallsState
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Callback
import retrofit2.Response

class CallsFragment : Fragment() {
    private lateinit var searchBar: EditText
    private lateinit var callsList: RecyclerView
    private val callsRepo = CallsRepo.getInstance()
    private val callsArrayList = arrayListOf<Call>()
    private var isFragmentActive = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calls, container, false)

        searchBar = root.findViewById(R.id.calls_search_bar)
        callsList = root.findViewById(R.id.calls_list)
        callsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        searchBar.doAfterTextChanged { getCallsList() }

        return root
    }

    private fun requestsTask(): Job = CoroutineScope(Dispatchers.Main).launch {
        while (isFragmentActive) {
            Log.d("mTag", "Sent calls get request (query - ${searchBar.text})")
            getCallsList()
            delay(Config.REQUESTS_DELAY)
        }
    }

    private fun getCallsList() {
        if (CurrentUser.login != "null") {
            callsArrayList.clear()
            callsRepo.callsService!!
                    .getUserCalls(
                            login = CurrentUser.login.toString(),
                            query = searchBar.text.toString()
                    ).enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                                call: retrofit2.Call<JsonObject>,
                                response: Response<JsonObject>
                        ) {
                            try {
                                val jsonParser = JsonParser()
                                val callsJsonStr = Gson().toJson(response.body())
                                val callsJson = jsonParser.parse(callsJsonStr).asJsonObject
                                val calls = callsJson.getAsJsonArray("calls")
                                for (i in 0..calls.size().minus(1)) {
                                    val callJson: JsonObject = calls.get(i).asJsonObject
                                    val thisCall = Call(
                                            profileUsername = callJson.get("username").asString,
                                            callsState = when (callJson.get("call_status").asString) {
                                                "incoming" -> CallsState.INCOMING
                                                "outgoing" -> CallsState.OUTGOING
                                                "unanswered" -> CallsState.UNANSWERED
                                                else -> CallsState.MISSED
                                            },
                                            callTime = callJson.get("call_time").asString,
                                            profileImage = callJson.get("profile_image").asString
                                    )
                                    callsArrayList.add(thisCall)
                                }
                                fillCallsList(callsArrayList)
                            } catch (e: Exception) {
                                Log.e("mTag", "Fetch Error", e)
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                            Log.e("mTag", "Fetch error", t)
                        }
                    })
        }
    }

    private fun fillCallsList(data: List<Call>) {
        callsList.adapter = CallsAdapter(data)
    }

    override fun onStop() {
        super.onStop()
        isFragmentActive = false
    }

    override fun onResume() {
        super.onResume()
        isFragmentActive = true
        requestsTask()
    }
}