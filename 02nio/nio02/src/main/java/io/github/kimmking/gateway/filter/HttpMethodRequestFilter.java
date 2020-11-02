package io.github.kimmking.gateway.filter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * 过滤http请求，如果是Post就返回错误信息，get请求就通过
 */
public class HttpMethodRequestFilter implements HttpRequestFilter {

    @Override
    public void filter(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        HttpMethod method = fullRequest.method();
        if (method.equals(HttpMethod.POST)) {
            FullHttpResponse response = null;
            try {
                response = new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
                response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            } finally {
                if (fullRequest != null) {
                    if (!HttpUtil.isKeepAlive(fullRequest)) {
                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        //response.headers().set(CONNECTION, KEEP_ALIVE);
                        ctx.write(response);
                    }
                }
                ctx.flush();
                ctx.close();
            }
        }
    }
}
