package org.starcoin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.starcoin.jsonrpc.JSONRPC2Request;
import org.starcoin.jsonrpc.JSONRPC2Response;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.jsonrpc.client.JSONRPC2SessionException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JsonRpcClientUtils {
    private JsonRpcClientUtils() {
    }


    /**
     * JSON RPC call method 'contract.call_v2'.
     *
     * @param functionId function Id.
     * @param typeArgs   type arguments.
     * @param args       arguments.
     * @return JSON RPC response body.
     */
    public static String contractCallV2(JSONRPC2Session jsonRpcSession, String functionId, List<String> typeArgs, List<Object> args) {
        String method = "contract.call_v2";
        List<Object> params = getContractCallV2Params(functionId, typeArgs, args);
        return callForObject(jsonRpcSession, method, params, String.class);
    }

    public static <T> T contractCallV2(JSONRPC2Session jsonRpcSession, String functionId, List<String> typeArgs, List<Object> args, TypeReference<T> typeReference) {
        String method = "contract.call_v2";
        List<Object> params = getContractCallV2Params(functionId, typeArgs, args);
        return callForObject(jsonRpcSession, method, params, typeReference);
    }

    private static List<Object> getContractCallV2Params(String functionId, List<String> typeArgs, List<Object> args) {
        Map<String, Object> singleParamMap = new HashMap<>();
        singleParamMap.put("function_id", functionId);
        singleParamMap.put("type_args", typeArgs);
        singleParamMap.put("args", args);
        List<Object> params = Collections.singletonList(singleParamMap);
        return params;
    }

    public static <T> T callForObject(JSONRPC2Session jsonRpcSession, String method, List<Object> params, TypeReference<T> typeRef) {
        return callForObject(jsonRpcSession, method, params, (result) -> {
            return getObjectMapper().convertValue(result, typeRef);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T callForObject(JSONRPC2Session jsonRpcSession, String method, List<Object> params, Class<T> objectClass) {
        return callForObject(jsonRpcSession, method, params, (result) -> {
            if (objectClass.isAssignableFrom(result.getClass())) {
                return (T) result;
            }
            if (objectClass.isAssignableFrom(String.class)) {
                return (T) result.toString();
            }
            return getObjectMapper().convertValue(result, objectClass);
        });
    }

    private static <T> T callForObject(JSONRPC2Session jsonRpcSession,
                                       String method, List<Object> params,
                                       Function<Object, T> resultConverter) {
        JSONRPC2Request request = new JSONRPC2Request(method, params, System.currentTimeMillis());
        JSONRPC2Response response;
        try {
            response = jsonRpcSession.send(request);
        } catch (JSONRPC2SessionException e) {
            //LOG.error("JSON rpc error.", e);
            throw new RuntimeException(e);
        }
        if (response.indicatesSuccess()) {
            Object result = response.getResult();
            if (result != null) {
                try {
                    return resultConverter.apply(result);
                } catch (RuntimeException e) {
                    String msg = "resultConverter.apply(result) error. Response: " + response;
                    //LOG.error(msg);
                    throw new RuntimeException(msg, e);
                }
            } else {
                return null;
            }
        } else {
            String msg = "JSON RPC error. response error: '" + response.getError() + "'. Request: " + request
                    + ". Response: " + response;
            //LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
