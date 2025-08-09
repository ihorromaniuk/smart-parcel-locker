package core.basesyntax.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.userservice.dto.exception.ExceptionDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
        } catch (RuntimeException e) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status,
                                 HttpServletResponse response,
                                 RuntimeException ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ExceptionDto dto = new ExceptionDto(status, ex.getMessage());
        try {
            String json = objectMapper.writeValueAsString(dto);
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
