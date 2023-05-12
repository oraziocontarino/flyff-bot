import React, { useEffect } from 'react';
import { Layout, Row, Col } from 'antd';
import { Content, Footer } from 'antd/es/layout/layout';
import GlobalHotKeys from './components/GlobalHotKeys';
import './App.css';
import Pipe from './components/Pipe';
import { useFetchConfigurationQuery, useFetchWindowListQuery } from './api/pipeline/hooks';

const App:React.FC = () => {
  const {data, isLoading, isError, error} = useFetchConfigurationQuery();
  useFetchWindowListQuery(undefined, {pollingInterval: 3000, });
  //useFetchConfigurationQuery(undefined, {pollingInterval: 1000, });



  return (
    <Layout style={{height:"100vh"}}>
      <Content>
        <Row className='fb-padding-top fb-padding-left'>
          <Col><GlobalHotKeys /></Col>
        </Row>
        <Row>
          { data && data.map(configurations => (
            <Col className='fb-padding-top fb-padding-left' key={configurations.pipeline.id}>
              <Pipe id={configurations.pipeline.id} />
            </Col>
          ))}
        </Row>
      </Content>
      <Footer>Footer</Footer>
    </Layout>
  );
}

export default App;
