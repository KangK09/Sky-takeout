package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //select * from setmeal limit 0,10
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        //获取数据
        long total = page.getTotal();
        List<SetmealVO> list = page.getResult();
        return new PageResult(total,list);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //删除套餐
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);

            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            //删除关联菜品
            setmealDishMapper.deleteSetmealDish(id);
            //删除套餐
            setmealMapper.deleteSetmeal(id);
        }

    }

    /**
     * 根据套餐id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //根据id查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);

        //根据套餐id查询套餐菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishsById(id);
        //封装数据
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐基本信息
        setmealMapper.updateSetmeal(setmeal);
        //删除原有关联菜品
        setmealDishMapper.deleteSetmealDish(setmealDTO.getId());
        //重新插入菜品信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
            setmealDishMapper.insertRelation(setmealDishes);
        }
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
