package com.yiyuan.configSecurity;

import com.yiyuan.config.SecurityProperties;
import com.yiyuan.service.impl.OnlineUserService;
import com.yiyuan.utils.SpringContextHolder;
import com.yiyuan.vo.OnlineUserDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Token过滤器
 * [说明]如果请求携带了Token,则对其进行校验,Token合法则让Security授权
 * @author MoLi
 */
@Slf4j
public class TokenFilter extends GenericFilterBean {

    /**
     * Token服务类
     */
    private final TokenProvider tokenProvider;

    /**
     * 要求初始化此类必须提供Token服务类[TokenProvider]
     */
    TokenFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = resolveToken(httpServletRequest);
        String requestRri = httpServletRequest.getRequestURI();

        if(token != null){
            // 验证 token 是否存在
            OnlineUserDto onlineUserDto = null;
            try {
                //获取JWT参数配置类
                SecurityProperties properties = SpringContextHolder.getBean(SecurityProperties.class);
                //获取在线用户业务类
                OnlineUserService onlineUserService = SpringContextHolder.getBean(OnlineUserService.class);
                //从Redis中获取该用户的信息
                onlineUserDto = onlineUserService.getOne(properties.getOnlineKey() + token);
            } catch (ExpiredJwtException e) {
                log.error(e.getMessage());
            }
            if (onlineUserDto != null && StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                //获取身份认证对象
                Authentication authentication = tokenProvider.getAuthentication(token);
                //将认证对象赋予给当前的SecurityContext完成认证
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("将身份验证设置为的安全上下文 '{}', uri: {}", authentication.getName(), requestRri);
            } else {
                log.debug("找不到有效的JWT令牌, uri: {}", requestRri);
            }
        }

        //将请求转发给过滤器链下一个filter
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 获取请求中的Token令牌
     */
    private String resolveToken(HttpServletRequest request) {
        //获取Jwt参数配置类
        SecurityProperties properties = SpringContextHolder.getBean(SecurityProperties.class);
        //获取请求头中的令牌
        String bearerToken = request.getHeader(properties.getHeader());
        //如果令牌不为空并且令牌前缀合法
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(properties.getTokenStartWith())) {
            //返回不带前缀的令牌
            return bearerToken.substring(7);
        }
        return null;
    }
}
