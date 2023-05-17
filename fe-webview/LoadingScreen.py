screen = """
<!DOCTYPE html>
<html lang='en'>
  <head>
    <meta charset='utf-8' />
    <meta name='viewport' content='width=device-width, initial-scale=1' />
    <meta name='theme-color' content='#000000' />
    <title>FlyffBot</title>
    <style>
      .antd-gray-3 {
        background: #f5f5f5;
      }
      .lds-dual-ring {
        display: inline-block;
        width: 160px;
        height: 160px;
      }
      .lds-dual-ring:after {
        content: ' ';
        display: block;
        width: 128px;
        height: 128px;
        margin: 16px;
        border-radius: 50%;
        border: 12px solid #fff;
        border-color: #4096ff transparent #4096ff transparent;
        animation: lds-dual-ring 1.2s linear infinite;
      }

      .wrapper {
        width: 100%;
        height: 100vh;
        display: flex;
        flex-direction: row;
        justify-content: space-around;
        align-items: center;
      }

      .info {
        display: flex;
        flex-direction: column;
      }

      .spinner-wrapper > div {
        display: flex;
        flex-direction: row;
      }

      .spinner-steps {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        text-align: center;
        height: 100%;
        padding-top: 8px;
        padding-left: 24px;
      }

      .aaa {
        animation: spinner-pulse-text 1.2s linear infinite;
      }

      .fb-card-title {
        color: rgba(0, 0, 0, 0.88);
        font-weight: 600;
        font-size: 16px;
      }

      .with-text-wrapper {
        position: relative;
      }

      .with-text-content {
        position: absolute;
        height: 100%;
        width: 100%;
      }

      .loading-text {
        color: #003eb3;
      }

      .loading-text:after {
        content: ' .';
        animation: dots 1s steps(5, end) infinite;
      }

      @keyframes lds-dual-ring {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }

      @keyframes dots {
        0%,
        20% {
          color: #003eb300;
          text-shadow: 0.25em 0 0 #003eb300, 0.5em 0 0 #003eb300;
        }
        40% {
          color: #003eb3;
          text-shadow: 0.25em 0 0 #003eb300, 0.5em 0 0 #003eb300;
        }
        60% {
          text-shadow: 0.25em 0 0 #003eb3, 0.5em 0 0 #003eb300;
        }
        80%,
        100% {
          text-shadow: 0.25em 0 0 #003eb3, 0.5em 0 0 #003eb3;
        }
      }
    </style>
  </head>

  <body class='antd-gray-3'>
    <div class='wrapper'>
      <div class='info'>
        <div class='lds-dual-ring with-text-wrapper'>
          <div class='with-text-content'>
            <div class='spinner-steps fb-card-title'>
              <div class='loading-text'>Initializing</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
"""
