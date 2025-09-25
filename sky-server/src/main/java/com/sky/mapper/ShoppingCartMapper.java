package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 查询购物车菜品和套餐数据
     * @param shoppingCart
     */
    ShoppingCart selectBy(ShoppingCart shoppingCart);

    /**
     * 添加购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, dish_id, setmeal_id, dish_flavor, number, amount, create_time, user_id) " + "values (#{name}, #{image}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime}, #{userId})")
    void insertShoppingCart(ShoppingCart shoppingCart);

    /**
     * 更新购物车数据
     * @param cart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateShoppingCart(ShoppingCart cart);

    /**
     * 查询指定用户的购物车数据
     * @param currentId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{currentId}")
    List<ShoppingCart> list(Long currentId);

    /**
     * 清空当前用户的购物车数据
     * @param currentId
     */
    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void clean(Long currentId);

    /**
     * 删除购物车数据
     * @param id
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteById(Long id);
    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
