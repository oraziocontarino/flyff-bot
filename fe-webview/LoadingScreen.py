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
        width: 80px;
        height: 80px;
      }
      .lds-dual-ring:after {
        content: ' ';
        display: block;
        width: 64px;
        height: 64px;
        margin: 8px;
        border-radius: 50%;
        border: 6px solid #fff;
        border-color: #4096ff transparent #4096ff transparent;
        animation: lds-dual-ring 1.2s linear infinite;
      }
      @keyframes lds-dual-ring {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }

      .wrapper {
        width: 100%;
        height: 100vh;
        display: flex;
        flex-direction: row;
        justify-content: space-around;
        align-items: center;
      }

      .size-4 {
        width: 4px;
      }

      .info {
        display: flex;
        flex-direction: column;
      }

      .spinner-wrapper {
        display: flex;
        flex-direction: row;
        justify-content: space-around;
      }
      .spinner-wrapper > div {
        display: flex;
        flex-direction: row;
      }

      .spinner-steps {
        display: flex;
        flex-direction: row;
        justify-content: center;
        padding-top: 8px;
      }
    </style>
  </head>
  <body class='antd-gray-3'>
    <div class='wrapper'>
      <div class='info'>
        <div class='spinner-wrapper'>
          <div>
            <div class='lds-dual-ring'></div>
            <div class='size-4'>&nbsp;</div>
          </div>
        </div>
        <div class='spinner-steps'>Initializing...</div>
      </div>
    </div>
  </body>
</html>
"""