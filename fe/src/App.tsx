import React, { useEffect } from 'react';
import { Layout, Row, Col } from 'antd';
import { Content, Footer } from 'antd/es/layout/layout';
import GlobalHotKeys from './components/GlobalHotKeys';
import './App.css';
import Pipe from './components/Pipe';
import { FlyffBotActions } from './api/slice';
import { useDispatch } from 'react-redux';
import { useRequestConfiguration, useSocketConnect, useSubribeConfigurationUpdates, useSubribeWindowListUpdates } from './api/hooks/pipeline';



const App:React.FC = () => {
  const dispatch = useDispatch();
  const { isConnected } = useSocketConnect();
  const { configurations } = useSubribeConfigurationUpdates();
  const { windowList } = useSubribeWindowListUpdates();
  const { requestConfiguration } = useRequestConfiguration();


  useEffect(() => {
    if(isConnected){
      requestConfiguration();
    }
  }, [isConnected, requestConfiguration]);

  useEffect(() => {
    // Store to redux to share value to other components
    dispatch(FlyffBotActions.storeConnectionSatus({isConnected}));
  }, [dispatch, isConnected]);

  useEffect(() => {
    // Store to redux to share value to other components
    dispatch(FlyffBotActions.storeConfigurations(configurations));
  }, [dispatch, configurations]);

  useEffect(() => {
    // Store to redux to share value to other components
    dispatch(FlyffBotActions.storeWindowList(windowList));
  }, [dispatch, windowList]);

  return (
    <Layout style={{height:"100vh"}}>
      <Content>
        <Row className='fb-padding-top fb-padding-left'>
          <Col><GlobalHotKeys /></Col>
        </Row>
        <Row>
          { configurations && configurations.map(configuration => (
            <Col className='fb-padding-top fb-padding-left' key={configuration.pipeline.id}>
              <Pipe id={configuration.pipeline.id} />
            </Col>
          ))}
        </Row>
      </Content>
      <Footer>
        Footer -&gt; {isConnected ? 'connected' : 'not-connected'}
      </Footer>
    </Layout>
  );
}

export default App;
