package com.lcw.one.main.rest;

import com.lcw.one.base.utils.LoginUserUtils;
import com.lcw.one.login.util.UserUtils;
import com.lcw.one.sys.util.DictUtils;
import com.lcw.one.user.constant.UserInfoTypeEnum;
import com.lcw.one.user.constant.UserSupplierStatusEnum;
import com.lcw.one.user.entity.UserInfoEO;
import com.lcw.one.user.service.UserInfoEOService;
import com.lcw.one.util.bean.LoginUser;
import com.lcw.one.util.http.ResponseMessage;
import com.lcw.one.util.http.Result;
import com.lcw.one.util.utils.DateUtils;
import com.lcw.one.util.utils.RequestUtils;
import com.lcw.one.util.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "${restPath}/")
@Api(description = "初始化数据接口")
public class InitRestController {

    @Autowired
    private Environment environment;

    @Autowired
    private UserInfoEOService userInfoEOService;

    @ApiOperation(value = "获取初始化数据")
    @GetMapping("initData")
    public ResponseMessage<Map<String, Object>> initData(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        String token = LoginUserUtils.getTokenFromSession(request);
        LoginUser loginUser = LoginUserUtils.getCurrentUser(token);

        result.put("token", token);
        result.put("systemTime", DateUtils.dateToString(new Date()));
        result.put("userMenu", UserUtils.getMenuTree());
        result.put("userInfo", userInfo(loginUser.getUserId()));
        result.put("userOffice", UserUtils.getUserOffice());
        result.put("sysDict", DictUtils.getDictMap());
        result.put("sysSetting", listSetting(request));
        return Result.success(result);
    }

    private UserInfoEO userInfo(String userId) {
        return userInfoEOService.get(userId);
    }

    private Map<String, Object> listSetting(HttpServletRequest request) {
        Map<String, Object> settingMap = new HashMap<>();
        settingMap.put("appName", environment.getProperty("one.application.name"));
        settingMap.put("appShortName", environment.getProperty("one.application.shortName"));
        settingMap.put("appVersion", environment.getProperty("one.application.version"));
        String maxFileSizeShow = environment.getProperty("spring.http.multipart.max-file-size");
        settingMap.put("maxFileSize", getMaxFileSize(maxFileSizeShow));
        settingMap.put("maxFileSizeShow", maxFileSizeShow);
        settingMap.put("basePath", RequestUtils.getBasePath(request));
        return settingMap;
    }

    private static long getMaxFileSize(String maxFileSizeStr) {
        if(StringUtils.isEmpty(maxFileSizeStr)) {
            return 0;
        }

        long maxFileSize = 0;
        if (maxFileSizeStr.toUpperCase().endsWith("MB")) {
            int mb = Integer.parseInt(maxFileSizeStr.substring(0, maxFileSizeStr.length() - 2));
            maxFileSize = mb * 1024 * 1024L;
        } else if (maxFileSizeStr.toUpperCase().endsWith("KB")) {
            int kb = Integer.parseInt(maxFileSizeStr.substring(0, maxFileSizeStr.length() - 2));
            maxFileSize = kb * 1024;
        }
        return maxFileSize;
    }

}
