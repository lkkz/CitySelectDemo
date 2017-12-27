package com.cool.selectlibrary;

import java.util.List;

/**
 * Created by cool on 2017/12/26.
 */

public class Province {
    public String name;

    public List<City> city;

    static class City{
        public String name;
        public List<String> area;
    }
}
