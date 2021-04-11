package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictAccount;
import com.powerboot.domain.*;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.order.OrderGrabbingRequest;
import com.powerboot.request.order.OrderRequest;
import com.powerboot.request.user.TeamMemberRequest;
import com.powerboot.response.*;
import com.powerboot.service.*;
import com.powerboot.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 团队分润
 */

@RestController
@RequestMapping("/team")
@Api(tags = "团队分润")
public class TeamController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EhcacheService ehcacheService;
    @Autowired
    private BalanceService balanceService;
    private final BigDecimal hundred = new BigDecimal(100);

    @ApiOperation(value = "团队分润列表")
    @PostMapping("/teamInfoList")
    @ResponseBody
    public BaseResponse<TeamInfoDto> teamInfoList(@RequestBody @Valid BaseRequest param) {
        BaseResponse<TeamInfoDto> response = BaseResponse.success();
        TeamInfoDto teamInfoDto = new TeamInfoDto();
        Integer teamSize = 0;
        BigDecimal totalContribution = BigDecimal.ZERO;
        List<TeamDetailListDto> teamDetailListDtos = new ArrayList<>();


        Long userId = getUserId(param);

        List<UserDO> oneUserList = userService.getUserByParentId(userId);

        if (!CollectionUtils.isEmpty(oneUserList)) {
            TeamDetailListDto oneTeam = new TeamDetailListDto();
            oneTeam.setLv(1);
            oneTeam.setSize(oneUserList.size());

            HashMap<String, Object> map = new HashMap<>();
            map.put("oneId", userId);
            List<OrderDO> oneOrders = orderService.list(map);
            if (!CollectionUtils.isEmpty(oneOrders)) {
                oneTeam.setBonus(oneOrders.stream().map(OrderDO::getOneAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                totalContribution = totalContribution.add(oneTeam.getBonus());
            } else {
                oneTeam.setBonus(BigDecimal.ZERO);
            }

            teamDetailListDtos.add(oneTeam);

            teamSize += oneUserList.size();
            if (!CollectionUtils.isEmpty(oneUserList)) {
                List<UserDO> twoUserList = userService.getUserList(oneUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
                teamSize += twoUserList.size();

                TeamDetailListDto twoTeam = new TeamDetailListDto();
                twoTeam.setLv(2);
                twoTeam.setSize(twoUserList.size());

                map.clear();
                map.put("twoId", userId);
                List<OrderDO> twoOrders = orderService.list(map);
                if (!CollectionUtils.isEmpty(twoOrders)) {
                    twoTeam.setBonus(twoOrders.stream().map(OrderDO::getTwoAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                    totalContribution = totalContribution.add(twoTeam.getBonus());
                } else {
                    twoTeam.setBonus(BigDecimal.ZERO);
                }

                teamDetailListDtos.add(twoTeam);

                if (!CollectionUtils.isEmpty(twoUserList)) {
                    List<UserDO> threeUserList = userService.getUserList(twoUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
                    teamSize += threeUserList.size();

                    TeamDetailListDto threeTeam = new TeamDetailListDto();
                    threeTeam.setLv(3);
                    threeTeam.setSize(threeUserList.size());

                    map.clear();
                    map.put("threeId", userId);
                    List<OrderDO> threeOrders = orderService.list(map);
                    if (!CollectionUtils.isEmpty(threeOrders)) {
                        threeTeam.setBonus(threeOrders.stream().map(OrderDO::getThreeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                        totalContribution = totalContribution.add(threeTeam.getBonus());
                    } else {
                        threeTeam.setBonus(BigDecimal.ZERO);
                    }

                    teamDetailListDtos.add(threeTeam);
                }
            }
        }

        teamInfoDto.setTeamSize(teamSize);
        teamInfoDto.setTotalContribution(totalContribution);

        teamInfoDto.setTeamDetailListDtos(teamDetailListDtos);

        response.setResultData(teamInfoDto);
        return response;
    }

    @ApiOperation(value = "团队分润列表")
    @PostMapping("/teamMemberList")
    @ResponseBody
    public BaseResponse<TeamPageDto> teamMemberList(@RequestBody @Valid TeamMemberRequest param) {
        BaseResponse<TeamPageDto> response = BaseResponse.success();
        Long userId = getUserId(param);
        UserDO userDO = userService.get(userId);
        TeamPageDto teamPageDto = new TeamPageDto();
        List<TeamMemberDto> teamMemberDtoList = new ArrayList<>();
        Integer totalCount = 0;


        Map<String, Object> map = new HashMap<>();
        //获取列表
        map.put("offset", (param.getPage() - 1) * param.getPageSize());
        map.put("limit", param.getPageSize());
        //一级分销的团队成员

        if (param.getLv().equals(1)) {
            map.put("parentId", userDO.getId());
            List<UserDO> count = userService.getUserByParentId(userId);
            totalCount = count.size();
            List<UserDO> oneUserList = userService.getUserByParentIdByPage(map);
            oneUserList.forEach(o -> {
                teamMemberDtoList.add(orderService.getDetailByUserId(o, param.getLv(),userId));
            });

        } else if (param.getLv().equals(2)) {
            List<UserDO> oneUserList = userService.getUserByParentId(userId);
            List<UserDO> count = userService.getUserList(oneUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
            totalCount = count.size();
            List<UserDO> twoUserList = userService.getUserListByPage(oneUserList.stream().map(UserDO::getId).collect(Collectors.toList()), (param.getPage() - 1) * param.getPageSize(), param.getPageSize());
            twoUserList.forEach(o -> {
                teamMemberDtoList.add(orderService.getDetailByUserId(o, param.getLv(),userId));
            });

        } else if (param.getLv().equals(3)) {
            List<UserDO> oneUserList = userService.getUserByParentId(userId);
            List<UserDO> twoUserList = userService.getUserList(oneUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
            List<UserDO> count = userService.getUserList(twoUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
            totalCount = count.size();
            List<UserDO> threeUserList = userService.getUserListByPage(twoUserList.stream().map(UserDO::getId).collect(Collectors.toList()), (param.getPage() - 1) * param.getPageSize(), param.getPageSize());
            threeUserList.forEach(o -> {
                teamMemberDtoList.add(orderService.getDetailByUserId(o, param.getLv(),userId));
            });
        } else if (param.getLv().equals(0)) {
            //查询全部
            List<UserDO> allList = new ArrayList<>();
            List<UserDO> oneUserList = userService.getUserByParentId(userId);
            if(!CollectionUtils.isEmpty(oneUserList)){
                List<UserDO> twoUserList = userService.getUserList(oneUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
                allList.addAll(twoUserList);
                if(!CollectionUtils.isEmpty(twoUserList)){
                    List<UserDO> threeUserList = userService.getUserList(twoUserList.stream().map(UserDO::getId).collect(Collectors.toList()));
                    allList.addAll(threeUserList);
                }
            }

            allList.addAll(oneUserList);
            totalCount = allList.size();

            allList = allList.stream().sorted(Comparator.comparing(UserDO::getId)).skip((param.getPage()-1) * param.getPageSize()).limit(param.getPageSize()).collect(Collectors.toList());
            allList.forEach(o -> {
                teamMemberDtoList.add(orderService.getDetailByUserId(o, param.getLv(),userId));
            });


        }


        Integer totalPages = totalCount / param.getPageSize();
        if (totalCount % param.getPageSize() != 0) {
            totalPages++;
        }
        teamPageDto.setTotalPage(totalPages);
        teamPageDto.setPage(param.getPage());
        teamPageDto.setPageSize(param.getPageSize());
        teamPageDto.setTeamMemberDtoList(teamMemberDtoList);

        response.setResultData(teamPageDto);
        return response;
    }


}
