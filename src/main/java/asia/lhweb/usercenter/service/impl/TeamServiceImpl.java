package asia.lhweb.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import asia.lhweb.usercenter.model.domain.Team;
import asia.lhweb.usercenter.service.TeamService;
import asia.lhweb.usercenter.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-04-24 20:16:45
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




