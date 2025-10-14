package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
//        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
//        Page<Category> categoryList = categoryMapper.categoryPageQuery(categoryPageQueryDTO);
//        return new PageResult(categoryList.getTotal(),categoryList);

        Page< Category> page = Page.of(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(categoryPageQueryDTO.getName()), Category::getName, categoryPageQueryDTO.getName())
                .eq(categoryPageQueryDTO.getType() != null, Category::getType, categoryPageQueryDTO.getType()).orderByAsc(Category::getSort);
        Page<Category> pageResult = categoryMapper.selectPage(page,queryWrapper);
        return new PageResult(pageResult.getTotal(),pageResult.getRecords());
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            throw new DeletionNotAllowedException("当前分类下关联了商品，不能删除");
        }
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            throw new DeletionNotAllowedException("当前分类下关联了套餐，不能删除");
        }
        categoryMapper.deleteById( id);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.updateById(category);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
                categoryMapper.updateById(category);
    }

    /**
     * 查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType,type);
        return categoryMapper.selectList(queryWrapper);
    }

}
