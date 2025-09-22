package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    //批量插入菜品口味
    void insertBatch(List<DishFlavor> dishFlavorList);


    //批量删除菜品口味
    void deleteBatch(List<Long> dishIds);

    //根据菜品id查询菜品口味
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getById(Long dishId);

    //根据菜品id删除菜品口味
    @Select("delete from dish_flavor where dish_id= #{dishId}")
    void deleteByDishId(Long dishId);

}
