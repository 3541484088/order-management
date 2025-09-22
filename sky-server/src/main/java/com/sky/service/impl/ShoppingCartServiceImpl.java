package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

            shoppingCart.setUserId(BaseContext.getCurrentId());
            ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);
            if(cart == null){
                if(shoppingCart.getDishId() != null){
                    Dish dish =dishMapper.getById (shoppingCart.getDishId());
                    shoppingCart.setName(dish.getName());
                    shoppingCart.setImage(dish.getImage());
                    shoppingCart.setAmount(dish.getPrice());
                }else{
                    Setmeal setmeal =setmealMapper.getById(shoppingCart.getSetmealId());
                    shoppingCart.setName(setmeal.getName());
                    shoppingCart.setImage(setmeal.getImage());
                    shoppingCart.setAmount(setmeal.getPrice());
                }
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCartMapper.insertShoppingCart(shoppingCart);
            }else{
                cart.setNumber(cart.getNumber()+1);
                shoppingCartMapper.updateShoppingCart(cart);
            }

    }


    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        return  shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 构造查询条件
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询购物车中是否存在该商品
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);

        if (cart != null) {
            // 如果商品数量大于1，减少数量
            if (cart.getNumber() > 1) {
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateShoppingCart(cart);
            } else {
                // 如果商品数量为1，直接删除该商品
                shoppingCartMapper.deleteById(cart.getId());
            }
        }
    }
}
