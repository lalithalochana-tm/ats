/*
 * MIT License
 *
 * Copyright (c) 2022 TestLeaf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.enums;

import java.security.SecureRandom;
import java.util.function.Supplier;

public enum Country implements Supplier<String> {
	  
		USA("United States"),
	    ALBANIA("Albania"),
	    ALGERIA("Algeria"),
	    ARGENTINA("Argentina"),
	    ARMENIA("Armenia"),
	    AUSTRIA("Austria"),
	    BANGLADESH("Bangladesh"),
	    BRAZIL("Brazil"),
	    CANADA("Canada"),
	    CHILE("Chile"),
	    CHINA("China"),
	    CUBA("Cuba"),
	    EGYPT("Egypt"),
	    FRANCE("France"),
	    INDIA("India"),
	    IRAN("Iran"),
	    IRAQ("Iraq"),
	    KENYA("Kenya"),
	    NEPAL("Nepal"),
	    PORTUGAL("Portugal"),
	    SWEDEN("Sweden"),
	    UNITED_ARAB_EMIRATES("United Arab Emirates"),
	    UK("UK"),
	    ZIMBABWE("Zimbabwe");
	
    private String country;
 
    Country(String country) {
        this.country = country;
    }
 
    public static Country getRandom() {
    	return values()[new SecureRandom().nextInt(values().length)];
    }

    @Override
    public String get() {
        return this.country;
    }

}