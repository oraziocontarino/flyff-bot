import { PauseCircleOutlined, LoadingOutlined } from "@ant-design/icons";
import { Col, Row } from "antd";
import { useMemo } from "react";
import { ActionStatus } from "../../api/types";

interface FBCardTitleProps {
  title: string;
  status?: ActionStatus;
}

export const FBCardTitle: React.FC<FBCardTitleProps> = ({
  title,
  status = ActionStatus.INVISIBLE,
}) => {
  const titleNode = useMemo(
    () => <div className="fb-card-title">{title}</div>,
    [title]
  );

  if (status === ActionStatus.INVISIBLE) {
    return titleNode;
  }

  return (
    <Row justify={"space-between"}>
      <Col className="fb-card-title">{title}</Col>
      <Col>
        {status === ActionStatus.PAUSED ? (
          <PauseCircleOutlined />
        ) : (
          <LoadingOutlined />
        )}
      </Col>
    </Row>
  );
};
