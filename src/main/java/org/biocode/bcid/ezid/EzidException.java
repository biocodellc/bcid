/**
 * This work was created by the National Center for Ecological Analysis and Synthesis
 * at the University of California Santa Barbara (UCSB).
 *
 *   Copyright 2011 Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.biocode.bcid.ezid;

/**
 * An exception that encapsulates errors from the EZID service.
 */
public class EzidException extends Exception {

    public EzidException(String msg) {
        super(msg);
    }

    public EzidException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EzidException(Throwable cause) {
        super(cause);
    }
}
