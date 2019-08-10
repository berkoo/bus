/*
 * The MIT License
 *
 * Copyright (c) 2017, aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.extra.mail;

import org.aoju.bus.core.lang.exception.CommonException;

/**
 * 全局邮件帐户，依赖于邮件配置文件{@link MailAccount#MAIL_SETTING_PATH}或{@link MailAccount#MAIL_SETTING_PATH2}
 *
 * @author Kimi Liu
 * @version 3.0.0
 * @since JDK 1.8
 */
public enum GlobalMailAccount {

    INSTANCE;

    private final MailAccount mailAccount;

    /**
     * 构造
     */
    private GlobalMailAccount() {
        mailAccount = createDefaultAccount();
    }

    /**
     * 获得邮件帐户
     *
     * @return 邮件帐户
     */
    public MailAccount getAccount() {
        return this.mailAccount;
    }

    /**
     * 创建默认帐户
     *
     * @return MailAccount
     */
    private MailAccount createDefaultAccount() {
        MailAccount mailAccount;
        try {
            mailAccount = new MailAccount(MailAccount.MAIL_SETTING_PATH);
        } catch (CommonException e) {
            mailAccount = new MailAccount(MailAccount.MAIL_SETTING_PATH2);
        }
        return mailAccount;
    }
}