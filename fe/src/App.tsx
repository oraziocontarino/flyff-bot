import React from "react";
import { Layout, Row, Col } from "antd";
import { Content } from "antd/es/layout/layout";
import GlobalHotKeys from "./components/GlobalHotKeys";
import "./App.css";
import Pipe from "./components/Pipe";
import { selectors } from "./api/slice";
import { useInitSocket } from "./api/hooks/app";
import { useSelector } from "react-redux";

const App: React.FC = () => {
  useInitSocket();
  const pipelines = useSelector(selectors.pipelinesSelector);

  return (
    <Layout className="height-100-vh">
      <Content className="overflow-auto">
        <Row className="fb-padding-top fb-padding-left">
          <Col>
            <GlobalHotKeys />
          </Col>
        </Row>
        <Row>
          {pipelines.map((configuration, i) => (
            <Col
              className="fb-padding-top fb-padding-left"
              key={configuration.pipeline.id}
            >
              <Pipe id={configuration.pipeline.id} i={i} />
            </Col>
          ))}
        </Row>
      </Content>
    </Layout>
  );
};

export default App;
