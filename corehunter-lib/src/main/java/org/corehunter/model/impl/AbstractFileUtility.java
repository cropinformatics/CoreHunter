// Copyright 2012 Guy Davenport, Herman De Beukelaer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.corehunter.model.impl;

import java.io.File;

import org.corehunter.model.Data;

public abstract class AbstractFileUtility<DataType extends Data> {

    public static final char COMMA_DELIMITER = ',';
    public static final char TAB_DELIMITER = '\t';
    public static final char SPACE_DELIMITER = ' ';
    
    private File file;

    public AbstractFileUtility(File file) {
        super();
        this.file = file;
    }

    public final File getFile() {
        return file;
    }

    public final void setFile(File file) {
        this.file = file;
    }
    
}
