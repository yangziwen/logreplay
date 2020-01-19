<style>
.el-header,
.el-footer {
  background-color: #ffd04b;
  color: #545c64;
  text-align: center;
  line-height: 60px;
  font-size: 28px;
  font-weight: bolder;
}

.el-aside {
  color: #333;
  background-color: #545c64;
  height: 1000px;
}

body > .el-container {
  margin-bottom: 40px;
}
.el-menu {
  border-right-width: 0px;
}
:any-link, :any-link:active { 
    color: inherit;
    text-decoration: inherit
}
</style>
<template>
  <el-container>
    <el-header>客户端日志埋点校验系统</el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          default-active="2"
          background-color="#545c64"
          text-color="#ffffff"
          active-text-color="#ffd04b"
          @open="handleOpen"
          @close="handleClose"
        >
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-location"></i>
              <span>埋点信息管理</span>
            </template>
            <el-menu-item index="1-1">
                <router-link to="/test">页面信息管理</router-link>    
            </el-menu-item>
            <el-menu-item index="1-2">操作项管理</el-menu-item>
            <el-menu-item index="1-3">公共操作项管理</el-menu-item>
          </el-submenu>
          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-location"></i>
              <span>埋点信息校验</span>
            </template>
            <el-menu-item index="2-1">实时校验</el-menu-item>
            <el-menu-item index="2-2">校验结果</el-menu-item>
            <el-menu-item index="2-3">校验状态查询</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
module.exports = {
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    }
  },
  router: new VueRouter({
      routes: [{
          path: '/test', component: httpVueLoader("vue/test.vue")
      }]
  })
};
</script>