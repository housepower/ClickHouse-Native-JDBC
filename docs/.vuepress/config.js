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
          { text: 'Home', link: '/' },
          { text: 'GitHub', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/': [/* ... */],
          '/nested/': [/* ... */]
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
          { text: '主页', link: '/zh/' },
          { text: 'GitHub', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/zh/': [/* ... */],
          '/zh/nested/': [/* ... */]
        }
      }
    }
  }
}
