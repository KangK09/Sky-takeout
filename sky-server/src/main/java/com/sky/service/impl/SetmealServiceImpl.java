package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {


    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @Override
    public void addCategory(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //属性拷贝
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.addCategory(setmeal);

        //获取id
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmeals = setmealDTO.getSetmealDishes();
        //给每个菜品赋值套餐id
        setmeals.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});

        //保存套餐和菜品的关联关系
        setmealDishMapper.insertRelation(setmeals);

    }
}
