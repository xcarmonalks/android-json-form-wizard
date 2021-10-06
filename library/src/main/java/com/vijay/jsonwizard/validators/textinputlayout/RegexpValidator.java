/**
 * Created by landa95 30/09/2021
 *
 * Copyright 2014 rengwuxian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Extracted from:
 * com.rengwuxian.materialedittext.validation.RegexpValidator
 */

package com.vijay.jsonwizard.validators.textinputlayout;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

public class RegexpValidator extends TILValidator{

    private Pattern pattern;

    public RegexpValidator(String errorMessage, String pattern){
        super(errorMessage);
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean var2) {
        return this.pattern.matcher(text).matches();
    }
}
