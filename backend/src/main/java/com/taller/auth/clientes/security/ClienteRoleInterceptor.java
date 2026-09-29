package com.taller.auth.clientes.security;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;
import java.util.Set;
@Component public class ClienteRoleInterceptor implements HandlerInterceptor {
 private final JwtService jwt; public ClienteRoleInterceptor(JwtService jwt){this.jwt=jwt;}
 public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler)throws Exception{
  if("OPTIONS".equalsIgnoreCase(req.getMethod()))return true;
  String h=req.getHeader("Authorization");
  if(h==null||!h.startsWith("Bearer "))return reject(res,401,"Se requiere JWT.");
  try{var c=jwt.verify(h.substring(7));if(!Set.of("ADMINISTRADOR","RECEPCIONISTA").contains(c.rol().toUpperCase()))return reject(res,403,"Rol sin permiso.");return true;}
  catch(RuntimeException e){return reject(res,401,"JWT invalido o vencido.");}
 }
 private boolean reject(HttpServletResponse r,int s,String m)throws Exception{r.setStatus(s);r.setContentType("application/json;charset=UTF-8");r.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(Map.of("mensaje",m)));return false;}
}
