package com.udacity.sandarumk.dailydish;

import lombok.Builder;
import lombok.Getter;

@Builder
public class TempObject {



        @Getter
        String date;

        @Getter
        String[] breakfast;

        @Getter
        String[] lunch;

        @Getter
        String[] dinner;


}
