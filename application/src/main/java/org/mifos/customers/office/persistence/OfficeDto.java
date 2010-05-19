/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.office.persistence;

import org.mifos.framework.business.service.DataTransferObject;

public class OfficeDto implements DataTransferObject{

    private final Short id;
    private final String name;
    private final String searchId;

    public OfficeDto(final Short officeId, String officeName, String searchId) {
        this.id = officeId;
        this.name = officeName;
        this.searchId = searchId;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public Short getId() {
        return this.id;
    }

    public String getName() {
        return this.name.trim();
    }

    public String getText() {
        return this.name.trim();
    }
}
