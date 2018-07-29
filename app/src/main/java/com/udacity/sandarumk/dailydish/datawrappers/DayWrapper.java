package com.udacity.sandarumk.dailydish.datawrappers;

import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DayWrapper implements Serializable{

    private Date date;

    private Map<MealTime,List<Recipe>> schedule;
}
