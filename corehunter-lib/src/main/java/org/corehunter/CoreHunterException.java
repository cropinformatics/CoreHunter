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

package org.corehunter;

public class CoreHunterException extends Exception
{

    private static final long serialVersionUID = 1L;

    public CoreHunterException(String message)
    {
            super(message);
    }

    public CoreHunterException(Throwable cause)
    {
            super(cause.getLocalizedMessage(), cause);
    }

    public CoreHunterException(String message, Throwable cause)
    {
            super(message, cause);
    }

}
