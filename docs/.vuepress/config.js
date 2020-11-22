module.exports = {
  base: '/ClickHouse-Native-JDBC/',
  title: 'ClickHouse Native JDBC',
  evergreen: true,
  locales: {
    '/': {
      lang: 'en-US',
      description: 'ClickHouse Native Protocol JDBC implementation'
    },
    '/zh/': {
      lang: 'zh-CN',
      description: 'ClickHouse 原生 JDBC 驱动实现'
    }
  },
  themeConfig: {
    locales: {
      '/': {
        selectText: 'Languages',
        label: 'English',
        ariaLabel: 'Languages',
        editLinkText: 'Edit this page on GitHub',
        serviceWorker: {
          updatePopup: {
            message: "New content is available.",
            buttonText: "Refresh"
          }
        },
        algolia: {},
        nav: [
          { text: 'Guide', link: '/guide/' },
          { text: 'Dev', link: '/dev/' },
          { text: 'Deep Dive', link: '/deep-dive/' },
          { text: 'Release Notes', link: '/release-notes/'},
          { text: 'GitHub', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/guide/': [
            {
              title: 'Quick Start',
              collapsable: false,
              sidebarDepth: 1,
              children: [],
            },
            {
              title: 'JDBC Driver',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            },
            {
              title: 'Spark Integration',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            },
            {
              title: 'Troubleshooting',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            }
          ],
          '/dev/': [
            {
              title: 'Pull Request',
              collapsable: false,
              sidebarDepth: 1,
              children: [],
            },
            {
              title: 'Release',
              collapsable: false,
              sidebarDepth: 1,
              children: [
                ['internal_release', 'Internal Release'],
                ['public_release', 'Public Release'],
              ],
            },
          ],
          '/deep-dive/': [
            {
              title: 'ClickHouse',
              collapsable: false,
              sidebarDepth: 2,
              children: [
                ['data_type', 'DataType'],
                ['native_protocol', 'Native Protocol'],
              ],
            }
          ],
          '/release-notes/': [],
        }
      },
      '/zh/': {
        selectText: '选择语言',
        label: '简体中文',
        editLinkText: '在 GitHub 上编辑此页',
        serviceWorker: {
          updatePopup: {
            message: "发现新内容可用.",
            buttonText: "刷新"
          }
        },
        algolia: {},
        nav: [
          { text: '指南', link: '/zh/guide/' },
          { text: '开发', link: '/zh/dev/' },
          { text: '深入', link: '/zh/deep-dive/' },
          { text: '发行注记', link: '/zh/release-notes/' },
          { text: '源码仓库', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/zh/guide/': [
            {
              title: '快速入门',
              collapsable: false,
              sidebarDepth: 1,
              children: [],
            },
            {
              title: 'JDBC 驱动',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            },
            {
              title: 'Spark 集成',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            },
            {
              title: '常见问题',
              children: [ /* ... */],
              initialOpenGroupIndex: -1
            }
          ],
          '/zh/dev/': [
            {
              title: 'Pull Request',
              collapsable: false,
              sidebarDepth: 1,
              children: [],
            }
          ],
          '/zh/deep-dive/': [
            {
              title: '工作原理',
              collapsable: false,
              sidebarDepth: 1,
              children: [],
            }
          ],
          '/zh/release-notes/': [],
        }
      }
    }
  }
}
