import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";

type OverlayWrapperProps = {
  children?: JSX.Element;
  i: number;
};

export const OverlayWrapper: React.FC<OverlayWrapperProps> = ({
  children,
  i,
}) => {
  const { t } = useTranslation();
  return (
    <div className="fb-overlay-wrapper">
      <div className="fb-overlay-content">
        <Row
          justify={"center"}
          align="middle"
          className={"height-100 color-red-5"}
        >
          <Col>{t("common.nohwnd")}</Col>
        </Row>
      </div>
      {children}
    </div>
  );
};
