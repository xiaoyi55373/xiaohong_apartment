/**
 * Navigation 导航组件库
 * 小红公寓claw - 品牌定制导航组件
 */

import SideBar from './SideBar.vue'
import NavBar from './NavBar.vue'
import TabsBar from './TabsBar.vue'
import Breadcrumb from './Breadcrumb.vue'
import SidebarItem from './SidebarItem.vue'

export {
  SideBar,
  NavBar,
  TabsBar,
  Breadcrumb,
  SidebarItem
}

export default {
  install(app: any) {
    app.component('SideBar', SideBar)
    app.component('NavBar', NavBar)
    app.component('TabsBar', TabsBar)
    app.component('Breadcrumb', Breadcrumb)
    app.component('SidebarItem', SidebarItem)
  }
}
