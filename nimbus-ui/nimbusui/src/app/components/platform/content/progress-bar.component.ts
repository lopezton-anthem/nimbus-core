/**
 * @license
 * Copyright 2016-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

import { Component, Input } from '@angular/core';
import { WebContentSvc } from '../../../services/content-management.service';
import { BaseElement } from '../base-element.component';
import { FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';

/**
 * \@author Tony Lopez
 * \@whatItDoes 
 * 
 * \@howToUse 
 * 
 */
@Component({
    selector: 'nm-progressBar',
    providers: [ WebContentSvc ],
    template:
    `   
        <ng-template [ngIf]="element?.visible">
            <nm-label [element]="element"></nm-label>
            <p-progressBar 
                [class]="element?.config?.uiStyles?.attributes?.cssClass"
                [value]="!form ? element?.leafState : formPercentageComplete"
                [showValue]="element?.config?.uiStyles?.attributes?.showValue"
                [unit]="element?.config?.uiStyles?.attributes?.unit"
                [mode]="element?.config?.uiStyles?.attributes?.mode"
                >
            </p-progressBar>
        </ng-template>
    `
})

export class ProgressBar extends BaseElement {

    @Input() form: FormGroup;
    formPercentageComplete: number;
    
    constructor(private webContentService: WebContentSvc) {
        super(webContentService);
    }

    ngOnInit() {
        super.ngOnInit();
        if (this.form) {
            this.formPercentageComplete = this.calculateFormCompletePercentage(this.form);
        }
        
        this.subscribers.push(this.form.valueChanges.subscribe(changes => {
            this.formPercentageComplete = this.calculateFormCompletePercentage(this.form);
        }));
    }

    calculateFormCompletePercentage(form: FormGroup) {
        let totalCount = 0;
        let validCount = 0;
        for(var control in form.controls) {
            if (form.controls[control].validator) {
                totalCount++;
                if (this.form.controls[control].valid) {
                    validCount++;
                }
            }
        }
        return 100 * (validCount / totalCount);
    }
}
