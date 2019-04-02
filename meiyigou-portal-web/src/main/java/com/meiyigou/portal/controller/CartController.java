package com.meiyigou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.meiyigou.pojogroup.Cart;
import com.meiyigou.usercart.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 6000)
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    // 跨域请求的注解方式实现Spring 4.2或以上版本支持
    // @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num){

        /*//跨域请求
        //可以访问的域：http://localhost:9105  可用*代替全部域
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9103");
        //允许使用携带凭证（cookie）
        response.setHeader("Access-Control-Allow-Credentials", "true");*/

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录名"+name);
        try {

            //1.从cookie中提出购物车
            List<Cart> cartList = findCartList();

            //2.调用服务方法操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if(name.equals("anonymousUser")) {
                //如果用户未登录
                //3.将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
                System.out.println("向cookie存储购物车");
            } else {
                cartService.saveCartListToRedis(name, cartList);
            }

            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }
    }

    /**
     * 从购物车提取购物车
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录名"+userName);

        String cookieName = "cartList";
        String cartListString = CookieUtil.getCookieValue(request, cookieName, "UTF-8");
        System.out.println("从cookie中读取购物车");

        if (cartListString == null || "".equals(cartListString)) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if(userName.equals("anonymousUser")) { //如果用户未登录
            //返回cookie中的购物车
            return cartList_cookie;

        } else { //用户登录
            //获取Redis购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(userName);

            if(cartList_cookie.size()>0) { //如果本地购物车中存在数据

                //合并购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                System.out.println("=====合并购物车");
                //更新Redis中的购物车
                cartService.saveCartListToRedis(userName, cartList);
                //本地购物车清除
                CookieUtil.deleteCookie(request, response, "cartList");
                //返回合并后的购物车
                return cartList;

            }

            return cartList_redis;
        }
    }
}
