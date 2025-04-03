package codes.spectrum.svdb

import io.grpc.*


// Вспомогательный интерфейс, возвращающий uidы текущей сессии и курсора
interface IUidsProvider {
    // возвращает текущий sessionUid
    fun getSessionUid(): String

    // возвращает текущий cursorUid
    fun getLastQueryCursorUid(): String
}

// Заголовок для авторизации
const val AUTHORIZATION_HEADER = "Authorization"

// Заголовок для имперсонации
const val IMPERSONATION_HEADER = "Impersonation"

// Заголовок с идентификатором сессии
const val SESSION_UID_HEADER = "sessionuid"

// Заголовок с идентификатором курсора
const val CURSOR_UID_HEADER = "cursoruid"

internal class SessionInterceptor(private val creds: SvdbCreds) : ClientInterceptor, IUidsProvider {

    private var _sessionUid: String = ""
    override fun getSessionUid(): String {
        return _sessionUid
    }

    private var _cursorUid: String = ""
    override fun getLastQueryCursorUid(): String {
        return _cursorUid
    }


    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object :
            ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(requireNotNull(next) {
                "interceptor called with null channel"
            }.newCall(method, callOptions)) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                requireNotNull(headers) {
                    "interceptor call metadata is null"
                }.put(
                    Metadata.Key.of(AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                    creds.toAuthorizationHeader()
                )
                if (creds.isImpersonation()) {
                    headers.put(
                        Metadata.Key.of(IMPERSONATION_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                        creds.toImpersonationHeader()
                    )
                }
                if (_sessionUid.isNotBlank()) {
                    headers.put(
                        Metadata.Key.of(SESSION_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                        _sessionUid
                    )
                }
                if (_cursorUid.isNotBlank()) {
                    headers.put(
                        Metadata.Key.of(CURSOR_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                        _cursorUid
                    )
                }
                super.start(SessionUidListener(responseListener), headers)
            }
        }
    }

    private inner class SessionUidListener<RespT : Any?>(parent: ClientCall.Listener<RespT>?) :
        ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(parent) {
        override fun onHeaders(headers: Metadata?) {
            if (headers != null) {
                if (headers[Metadata.Key.of(SESSION_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER)] != null) {
                    _sessionUid = headers[Metadata.Key.of(SESSION_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER)] ?: ""
                }
                if (headers[Metadata.Key.of(CURSOR_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER)] != null) {
                    _cursorUid = headers[Metadata.Key.of(CURSOR_UID_HEADER, Metadata.ASCII_STRING_MARSHALLER)] ?: ""
                }
            }
        }
    }
}
