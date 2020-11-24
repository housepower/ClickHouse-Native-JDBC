module.exports = {
  base: '/ClickHouse-Native-JDBC/',
  title: 'ClickHouse Native JDBC',
  evergreen: true,
  plugins: [
    'mermaidjs'
  ],
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
          { text: 'Guide', link: '/guide/introduction' },
          { text: 'Dev', link: '/dev/contribute' },
          { text: 'Deep Dive', link: '/deep-dive/native_protocol' },
          { text: 'Release Notes', link: '/release-notes/'},
          { text: 'GitHub', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/guide/': [
            ['introduction', 'Introduction'],
            {
              title: 'Usage',
              children: [
                ['jdbc_driver', 'JDBC Driver'],
                ['connection_pool', 'Connection Pool'],
                ['spark_integration', 'Spark Integration'],
              ],
            },
            ['troubleshooting', 'Troubleshooting'],
          ],
          '/dev/': [
            ['contribute', 'Contribute Guide'],
            {
              title: 'Release',
              children: [
                ['internal_release', 'Internal Release'],
                ['public_release', 'Public Release'],
              ],
            },
          ],
          '/deep-dive/': [
            {
              title: 'ClickHouse',
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
          { text: '指南', link: '/zh/guide/introduction' },
          { text: '开发', link: '/zh/dev/contribute' },
          { text: '深入', link: '/zh/deep-dive/native_protocol' },
          { text: '发行注记', link: '/zh/release-notes/' },
          { text: '源码仓库', link: 'https://github.com/housepower/ClickHouse-Native-JDBC' },
        ],
        sidebar: {
          '/zh/guide/': [
            ['introduction', '简介'],
            {
              title: '使用',
              children: [
                ['jdbc_driver', 'JDBC 驱动'],
                ['connection_pool', '连接池'],
                ['spark_integration', 'Spark 集成'],
              ],
            },
            ['troubleshooting', '常见问题'],
          ],
          '/zh/dev/': [
            ['contribute', '贡献指南'],
            {
              title: '发布',
              children: [
                ['internal_release', '内部发布'],
                ['public_release', '公开发布'],
              ],
            },
          ],
          '/zh/deep-dive/': [
            {
              title: 'ClickHouse',
              children: [
                ['data_type', '数据类型'],
                ['native_protocol', '原生协议'],
              ],
            }
          ],
          '/zh/release-notes/': [],
        }
      }
    }
  }
}
